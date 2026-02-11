const API_BASE = '/api';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export interface Classroom {
  id: string;
  name: string;
  subject: string;
  teacherId: string;
  period: number;
}

export interface Assignment {
  id: string;
  classId: string;
  title: string;
  description: string;
  dueDate: string;
  type: 'HOMEWORK' | 'QUIZ' | 'TEST' | 'PROJECT';
  estimatedWorkloadMinutes: number;
}

export interface TimelineItem {
  assignmentId: string;
  className: string;
  title: string;
  dueDate: string;
  workload: number;
  conflictLevel: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface User {
  id: string;
  email: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  createdAt: string;
}

export interface Enrollment {
  id: string;
  userId: string;
  classId: string;
}

class ApiClient {
  private token: string | null = null;

  constructor() {
    if (typeof window !== 'undefined') {
      this.token = localStorage.getItem('token');
    }
  }

  setToken(token: string) {
    this.token = token;
    if (typeof window !== 'undefined') {
      localStorage.setItem('token', token);
    }
  }

  clearToken() {
    this.token = null;
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
    }
  }

  private async request(endpoint: string, options: RequestInit = {}) {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...((options.headers as Record<string, string>) || {}),
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ error: 'Request failed' }));
      throw new Error(error.error || 'Request failed');
    }

    return response.json();
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    this.setToken(response.token);
    if (typeof window !== 'undefined') {
      localStorage.setItem('role', response.role);
    }
    return response;
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    this.setToken(response.token);
    if (typeof window !== 'undefined') {
      localStorage.setItem('role', response.role);
    }
    return response;
  }

  async getClasses(): Promise<Classroom[]> {
    return this.request('/classes');
  }

  async getAssignments(): Promise<Assignment[]> {
    return this.request('/assignments');
  }

  async getTimeline(): Promise<TimelineItem[]> {
    return this.request('/timeline');
  }

  // Admin endpoints
  async getAllUsers(): Promise<User[]> {
    return this.request('/admin/users');
  }

  async createUser(email: string, password: string, role: string): Promise<User> {
    return this.request('/admin/users', {
      method: 'POST',
      body: JSON.stringify({ email, password, role }),
    });
  }

  async updateUser(id: string, data: Partial<{ email: string; password: string; role: string }>): Promise<User> {
    return this.request(`/admin/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deleteUser(id: string): Promise<void> {
    return this.request(`/admin/users/${id}`, {
      method: 'DELETE',
    });
  }

  async getAllEnrollments(): Promise<Enrollment[]> {
    return this.request('/admin/enrollments');
  }

  async createEnrollment(userId: string, classId: string): Promise<Enrollment> {
    return this.request('/admin/enrollments', {
      method: 'POST',
      body: JSON.stringify({ userId, classId }),
    });
  }

  async deleteEnrollment(id: string): Promise<void> {
    return this.request(`/admin/enrollments/${id}`, {
      method: 'DELETE',
    });
  }

  async getAllClasses(): Promise<Classroom[]> {
    return this.request('/admin/classes');
  }
}

export const api = new ApiClient();
