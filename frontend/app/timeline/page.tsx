'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { api, TimelineItem } from '../../lib/api';

export default function TimelinePage() {
  const router = useRouter();
  const [timeline, setTimeline] = useState<TimelineItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<string>('all');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    loadTimeline();
  }, [router]);

  const loadTimeline = async () => {
    try {
      const data = await api.getTimeline();
      setTimeline(data);
    } catch (error) {
      console.error('Failed to load timeline:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    api.clearToken();
    router.push('/login');
  };

  const getConflictColor = (level: string) => {
    switch (level) {
      case 'HIGH':
        return 'bg-red-100 border-red-500 text-red-900';
      case 'MEDIUM':
        return 'bg-yellow-100 border-yellow-500 text-yellow-900';
      case 'LOW':
        return 'bg-green-100 border-green-500 text-green-900';
      default:
        return 'bg-gray-100 border-gray-500 text-gray-900';
    }
  };

  const filteredTimeline = filter === 'all' 
    ? timeline 
    : timeline.filter(item => item.conflictLevel === filter);

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
                onClick={() => router.push('/dashboard')}
                className="text-gray-700 hover:text-indigo-600"
              >
                Dashboard
              </button>
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
            <h2 className="text-3xl font-bold text-gray-900 mb-6">Assignment Timeline</h2>

            <div className="mb-6 flex space-x-2">
              <button
                onClick={() => setFilter('all')}
                className={`px-4 py-2 rounded-md ${
                  filter === 'all'
                    ? 'bg-indigo-600 text-white'
                    : 'bg-white text-gray-700 border border-gray-300'
                }`}
              >
                All
              </button>
              <button
                onClick={() => setFilter('HIGH')}
                className={`px-4 py-2 rounded-md ${
                  filter === 'HIGH'
                    ? 'bg-red-600 text-white'
                    : 'bg-white text-gray-700 border border-gray-300'
                }`}
              >
                High Conflict
              </button>
              <button
                onClick={() => setFilter('MEDIUM')}
                className={`px-4 py-2 rounded-md ${
                  filter === 'MEDIUM'
                    ? 'bg-yellow-600 text-white'
                    : 'bg-white text-gray-700 border border-gray-300'
                }`}
              >
                Medium Conflict
              </button>
              <button
                onClick={() => setFilter('LOW')}
                className={`px-4 py-2 rounded-md ${
                  filter === 'LOW'
                    ? 'bg-green-600 text-white'
                    : 'bg-white text-gray-700 border border-gray-300'
                }`}
              >
                Low Conflict
              </button>
            </div>

            {filteredTimeline.length === 0 ? (
              <p className="text-gray-500">No assignments found</p>
            ) : (
              <div className="space-y-4">
                {filteredTimeline.map((item, index) => (
                  <motion.div
                    key={item.assignmentId}
                    className={`border-l-4 rounded-lg shadow p-6 ${getConflictColor(item.conflictLevel)}`}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.3, delay: index * 0.05 }}
                    whileHover={{ scale: 1.02 }}
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <h3 className="text-lg font-semibold">{item.title}</h3>
                        <p className="text-sm mt-1">{item.className}</p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-medium">
                          {new Date(item.dueDate).toLocaleDateString()}
                        </p>
                        <p className="text-xs mt-1">
                          {new Date(item.dueDate).toLocaleTimeString()}
                        </p>
                      </div>
                    </div>
                    <div className="mt-4 flex justify-between items-center">
                      <span className="text-sm">
                        Estimated: {item.workload} min
                      </span>
                      <span className="text-xs font-semibold px-2 py-1 rounded">
                        {item.conflictLevel} CONFLICT
                      </span>
                    </div>
                  </motion.div>
                ))}
              </div>
            )}
          </div>
        </motion.div>
      </main>
    </div>
  );
}
