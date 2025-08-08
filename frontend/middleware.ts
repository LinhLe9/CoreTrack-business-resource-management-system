// middleware.ts
import { NextRequest, NextResponse } from 'next/server';

export function middleware(req: NextRequest) {
  // Note: Middleware runs on server-side, so we can't access localStorage
  // We'll rely on client-side authentication checks for now
  // The middleware will only handle basic routing protection
  
  const protectedPaths = ['/product', '/material', '/dashboard', '/admin'];
  const pathIsProtected = protectedPaths.some(path =>
    req.nextUrl.pathname.startsWith(path)
  );

  // Protect add routes - redirect to main pages if not authorized
  if (req.nextUrl.pathname === '/product/add' || req.nextUrl.pathname === '/material/add') {
    // Redirect to the main catalog pages - client-side will handle role-based access
    const redirectPath = req.nextUrl.pathname === '/product/add' ? '/product' : '/material';
    return NextResponse.redirect(new URL(redirectPath, req.url));
  }

  // Protect edit routes - redirect to detail pages if not authorized
  if (req.nextUrl.pathname.includes('/edit')) {
    // Extract the base path and ID from the edit URL
    const pathParts = req.nextUrl.pathname.split('/');
    if (pathParts.length >= 3) {
      const basePath = pathParts[1]; // 'product' or 'material'
      const id = pathParts[2]; // the ID
      const redirectPath = `/${basePath}/${id}`;
      return NextResponse.redirect(new URL(redirectPath, req.url));
    }
  }

  // Protect inventory initial routes - redirect to inventory pages if not authorized
  if (req.nextUrl.pathname === '/product-inventory/initial' || req.nextUrl.pathname === '/material-inventory/initial') {
    // Redirect to the main inventory pages - client-side will handle role-based access
    const redirectPath = req.nextUrl.pathname === '/product-inventory/initial' ? '/product-inventory' : '/material-inventory';
    return NextResponse.redirect(new URL(redirectPath, req.url));
  }

  // For now, let the client-side handle authentication
  // This prevents server-side redirects that might interfere with client-side auth
  return NextResponse.next();
}

export const config = {
  matcher: ['/product/:path*', '/material/:path*', '/dashboard/:path*', '/product-inventory/:path*', '/material-inventory/:path*'],
};
