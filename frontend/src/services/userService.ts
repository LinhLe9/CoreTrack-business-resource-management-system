import axios from '@/lib/axios';

export interface UserDetailResponse {
  id: number;
  username: string;
  email: string;
  role: 'OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF';
  enabled: boolean;
  createdByEmail: string | null;
  createdByUsername: string | null;
  createdAt: string;
}

export interface CreateUserRequest {
  email: string;
  role: 'OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF';
  createdBy: number;
}

class UserService {
  // Get all users (OWNER only)
  async getAllUsers(): Promise<UserDetailResponse[]> {
    try {
      console.log('Calling /admin/users endpoint...');
      const response = await axios.get('/admin/users');
      console.log('Users response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching users:', error);
      throw error;
    }
  }

  // Get current user details
  async getCurrentUserDetails(): Promise<UserDetailResponse> {
    try {
      const response = await axios.get('/auth/current-user');
      return response.data;
    } catch (error) {
      console.error('Error fetching current user details:', error);
      throw error;
    }
  }

  // Create new user (OWNER only)
  async createUser(createUserRequest: CreateUserRequest): Promise<string> {
    try {
      const response = await axios.post('/admin/create-user', createUserRequest);
      return response.data;
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  }

  // Logout user
  async logout(): Promise<string> {
    try {
      const response = await axios.post('/auth/logout');
      return response.data;
    } catch (error) {
      console.error('Error during logout:', error);
      throw error;
    }
  }
}

const userService = new UserService();
export default userService; 