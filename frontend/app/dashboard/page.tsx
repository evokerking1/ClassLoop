'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { api, Assignment, Classroom } from '../../lib/api';

export default function DashboardPage() {
  const router = useRouter();
  const [classes, setClasses] = useState<Classroom[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState(true);
  const [userRole, setUserRole] = useState<string>('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    const role = localStorage.getItem('role');
    setUserRole(role || '');

    loadData();
  }, [router]);

  const loadData = async () => {
    try {
      const [classesData, assignmentsData] = await Promise.all([
        api.getClasses(),
        api.getAssignments(),
      ]);
      setClasses(classesData);
      setAssignments(assignmentsData);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    api.clearToken();
    router.push('/login');
  };

  const upcomingAssignments = assignments
    .filter(a => new Date(a.dueDate) > new Date())
    .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
    .slice(0, 5);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="text-xl">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-indigo-600">ClassLoop</h1>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => router.push('/timeline')}
                className="text-gray-700 hover:text-indigo-600"
              >
                Timeline
              </button>
              {userRole === 'ADMIN' && (
                <button
                  onClick={() => router.push('/admin')}
                  className="text-gray-700 hover:text-indigo-600"
                >
                  Admin
                </button>
              )}
              <button
                onClick={handleLogout}
                className="text-gray-700 hover:text-indigo-600"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
        >
          <div className="px-4 py-6 sm:px-0">
            <h2 className="text-3xl font-bold text-gray-900 mb-6">Dashboard</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
              <motion.div
                className="bg-white overflow-hidden shadow rounded-lg"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.5, delay: 0.1 }}
              >
                <div className="px-4 py-5 sm:p-6">
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Total Classes
                  </dt>
                  <dd className="mt-1 text-3xl font-semibold text-gray-900">
                    {classes.length}
                  </dd>
                </div>
              </motion.div>

              <motion.div
                className="bg-white overflow-hidden shadow rounded-lg"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.5, delay: 0.2 }}
              >
                <div className="px-4 py-5 sm:p-6">
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Upcoming Assignments
                  </dt>
                  <dd className="mt-1 text-3xl font-semibold text-gray-900">
                    {upcomingAssignments.length}
                  </dd>
                </div>
              </motion.div>
            </div>

            <motion.div
              className="bg-white shadow rounded-lg"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.3 }}
            >
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  Upcoming Deadlines
                </h3>
                {upcomingAssignments.length === 0 ? (
                  <p className="text-gray-500">No upcoming assignments</p>
                ) : (
                  <ul className="divide-y divide-gray-200">
                    {upcomingAssignments.map((assignment, index) => (
                      <motion.li
                        key={assignment.id}
                        className="py-4"
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.3, delay: 0.4 + index * 0.1 }}
                      >
                        <div className="flex justify-between">
                          <div>
                            <p className="text-sm font-medium text-gray-900">
                              {assignment.title}
                            </p>
                            <p className="text-sm text-gray-500">
                              {assignment.type}
                            </p>
                          </div>
                          <div className="text-sm text-gray-500">
                            {new Date(assignment.dueDate).toLocaleDateString()}
                          </div>
                        </div>
                      </motion.li>
                    ))}
                  </ul>
                )}
              </div>
            </motion.div>
          </div>
        </motion.div>
      </main>
    </div>
  );
}
