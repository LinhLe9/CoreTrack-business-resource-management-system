'use client'; // Đây là Client Component
import LoginForm from '@/components/auth/LoginForm';

export default function TestPage() {
  return (
    <div>
      <p>This is a test page.</p>
      <LoginForm />
    </div>
  );
}