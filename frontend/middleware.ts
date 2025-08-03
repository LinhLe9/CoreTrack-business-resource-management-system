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

  // For now, let the client-side handle authentication
  // This prevents server-side redirects that might interfere with client-side auth
  return NextResponse.next();
}
export const config = {
  matcher: ['/product/:path*', '/material/:path*', '/dashboard/:path*'],
};
