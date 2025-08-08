'use client';

import { usePathname } from 'next/navigation';
import Header from './header';
import PublicHeader from './publicHeader';

export default function HeaderWrapper() {
  const pathname = usePathname();
  
  // Don't render header for auth pages (they have their own layout)
  if (pathname.startsWith('/login') || 
      pathname.startsWith('/register') || 
      pathname.startsWith('/validation') ||
      pathname.startsWith('/forgot-password') ||
      pathname.startsWith('/reset-password')) {
    return null;
  }
  
  // Use PublicHeader for home page, Header for other pages
  if (pathname === '/') {
    return <PublicHeader />;
  }
  
  return <Header />;
} 