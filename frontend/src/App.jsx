import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

function App() {
  const [todos, setTodos] = useState([]);
  const [task, setTask] = useState('');
  const [message, setMessage] = useState('');

  const fetchTodos = async () => {
    const res = await axios.get(`${API_BASE}/todos`);
    setTodos(res.data);
  };

  const addTodo = async () => {
    if (!task.trim()) return;
    await axios.post(`${API_BASE}/todos`, { task, completed: false });
    setTask('');
    fetchTodos();
  };

  const deleteTodo = async (id) => {
    await axios.delete(`${API_BASE}/todos/${id}`);
    fetchTodos();
  };

  const summarizeAndSend = async () => {
    try {
      await axios.post(`${API_BASE}/summarize`);
      setMessage('✅ Summary sent to Slack!');
    } catch (err) {
      setMessage('❌ Failed to send summary.');
    }
    setTimeout(() => setMessage(''), 3000);
  };

  useEffect(() => {
    fetchTodos();
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <h1 className="text-2xl font-bold mb-4">📝 Todo Summary Assistant</h1>
      <div className="flex gap-2 mb-4">
        <input
          className="border p-2 flex-1"
          value={task}
          onChange={(e) => setTask(e.target.value)}
          placeholder="Enter a new task"
        />
        <button onClick={addTodo} className="bg-blue-500 text-white px-4 py-2 rounded">
          Add
        </button>
      </div>
      <ul className="mb-6">
        {todos.map((todo) => (
          <li key={todo.id} className="flex justify-between items-center bg-white p-3 mb-2 rounded shadow">
            <span>{todo.task}</span>
            <button onClick={() => deleteTodo(todo.id)} className="text-red-500">❌</button>
          </li>
        ))}
      </ul>
      <button
        onClick={summarizeAndSend}
        className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
      >
        📤 Summarize & Send to Slack
      </button>
      {message && <p className="mt-4 font-semibold">{message}</p>}
    </div>
  );
}

export default App;