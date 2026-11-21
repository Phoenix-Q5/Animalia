import React from 'react';
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import axios from 'axios';
import AnimalForm from './AddAnimal';

jest.mock('axios', () => ({ create: jest.fn(), get: jest.fn() }));
jest.mock('@react-keycloak/web', () => ({
  useKeycloak: () => ({ keycloak: mockKeycloak })
}));
jest.mock('framer-motion', () => {
  const React = require('react');
  return {
    AnimatePresence: ({ children }) => children,
    motion: {
      div: ({ children, initial, animate, exit, transition, ...props }) => <div {...props}>{children}</div>
    }
  };
});

const mockKeycloak = { token: 'test-token' };
const mockApi = { get: jest.fn(), post: jest.fn() };

beforeEach(() => {
  sessionStorage.clear();
  jest.clearAllMocks();
  axios.create.mockReturnValue(mockApi);
  mockApi.get.mockResolvedValue({ data: {} });
});

test('submits a file, polls the job and shows the results', async () => {
  mockApi.post.mockResolvedValue({ data: { id: 'job-1', status: 'QUEUED' } });
  axios.get.mockResolvedValue({
    data: {
      status: 'COMPLETED',
      result: { totalRows: 1001, imported: 1000, failed: 1, rows: [] }
    }
  });
  render(<MemoryRouter><AnimalForm /></MemoryRouter>);
  fireEvent.click(screen.getByText('Bulk import'));
  const file = new File(['name\nlion'], 'animals.csv', { type: 'text/csv' });
  fireEvent.change(document.querySelector('input[type="file"][accept*=".csv"]'), { target: { files: [file] } });
  fireEvent.click(screen.getByText('Import catalog'));

  await screen.findByText('1000 imported');
  expect(screen.getByText('Import status: Completed')).toBeTruthy();
  expect(screen.getByText('1 failed')).toBeTruthy();
  expect(sessionStorage.getItem('animalia-bulk-import-job')).toBe('job-1');
  expect(axios.get).toHaveBeenCalledWith(
    expect.stringContaining('/admin/bulkImport/job-1'),
    expect.objectContaining({ headers: { Authorization: 'Bearer test-token' } })
  );
});

test('reconnects to a job and reports an unavailable status', async () => {
  sessionStorage.setItem('animalia-bulk-import-job', 'lost-job');
  axios.get.mockRejectedValue({ response: { status: 404 } });
  render(<MemoryRouter><AnimalForm /></MemoryRouter>);

  await screen.findByText(/Import status is no longer available/);
  expect(screen.getByText('Import status: Failed')).toBeTruthy();
  await waitFor(() => expect(sessionStorage.getItem('animalia-bulk-import-job')).toBeNull());
});

test('continues polling a running job until it completes', async () => {
  jest.useFakeTimers();
  sessionStorage.setItem('animalia-bulk-import-job', 'running-job');
  axios.get
    .mockResolvedValueOnce({ data: { status: 'RUNNING' } })
    .mockResolvedValueOnce({
      data: { status: 'COMPLETED', result: { totalRows: 1, imported: 1, failed: 0, rows: [] } }
    });
  render(<MemoryRouter><AnimalForm /></MemoryRouter>);

  await screen.findByText('Import status: In progress');
  await act(async () => { jest.advanceTimersByTime(3000); });
  expect(await screen.findByText('Import status: Completed')).toBeTruthy();
  expect(axios.get).toHaveBeenCalledTimes(2);
  jest.useRealTimers();
});
