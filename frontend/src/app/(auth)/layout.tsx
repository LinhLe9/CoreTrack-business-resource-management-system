import '../globals.css';
import PublicHeader from '../../components/general/publicHeader';

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <PublicHeader />
      {children}
    </>
  );
} 