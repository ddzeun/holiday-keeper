import axios from 'axios';
import type { Holiday, PageResponse, SearchParams, ApiResponse } from '../types/holiday';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const holidayApi = {
  // 공휴일 검색
  searchHolidays: async (params: SearchParams): Promise<PageResponse<Holiday>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Holiday>>>('/holidays', { params });
    // axios는 이미 response.data를 반환하므로, response.data.data가 아니라 response.data만 사용
    return response.data.data;
  },

  // 공휴일 갱신 (Refresh)
  refreshHolidays: async (year: number, countryCode: string): Promise<void> => {
    await apiClient.post<ApiResponse<void>>('/holidays/refresh', null, {
      params: { year, countryCode }
    });
  },

  // 공휴일 삭제
  deleteHolidays: async (year: number, countryCode: string): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>('/holidays', {
      params: { year, countryCode }
    });
  },
};