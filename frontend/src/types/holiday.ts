export const HolidayType = {
  PUBLIC: 'PUBLIC',
  BANK: 'BANK',
  SCHOOL: 'SCHOOL',
  AUTHORITIES: 'AUTHORITIES',
  OPTIONAL: 'OPTIONAL',
  OBSERVANCE: 'OBSERVANCE',
  UNKNOWN: 'UNKNOWN'
} as const;

export type HolidayType = typeof HolidayType[keyof typeof HolidayType];

export interface Holiday {
  date: string;
  localName: string;
  englishName: string;
  countryCode: string;
  fixed: boolean;
  globalHoliday: boolean;
  launchYear: number | null;
  types: HolidayType[];
  counties: string[];
}

// 백엔드 실제 응답 구조
export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

// 백엔드 ApiResponse wrapper
export interface ApiResponse<T> {
  success: boolean;
  code?: string | null;
  message?: string | null;
  data: T;
}

export interface SearchParams {
  countryCode?: string;
  from?: string;
  to?: string;
  type?: HolidayType;
  page?: number;
  size?: number;
}