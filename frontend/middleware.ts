// middleware.ts
import { NextRequest, NextResponse } from 'next/server';

export function middleware(req: NextRequest) {
  const token = req.cookies.get('token')?.value;

  const protectedPaths = ['/product', '/material', '/dashboard'];
  const pathIsProtected = protectedPaths.some(path =>
    req.nextUrl.pathname.startsWith(path)
  );

  if (pathIsProtected && !token) {
    // Nếu không có token mà truy cập đường dẫn bảo vệ thì redirect về login
    return NextResponse.redirect(new URL('/login', req.url));
  }

  // Nếu có token hoặc không phải đường dẫn bảo vệ thì cho qua
  return NextResponse.next();
}

// Chỉ áp dụng middleware cho những đường dẫn này
export const config = {
  matcher: ['/product/:path*', '/material/:path*', '/dashboard/:path*'],
};
