export interface User {
  id?: number;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'ADMIN' | 'WARDEN' | 'STUDENT';
  verified?: boolean;
  roomId?: number;
}
