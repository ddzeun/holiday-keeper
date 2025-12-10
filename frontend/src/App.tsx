import { useState, useEffect } from 'react';
import { holidayApi } from './api/holidayApi';
import { HolidayType } from './types/holiday';
import type { Holiday, SearchParams } from './types/holiday';
import './App.css';

function App() {
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 검색 파라미터
  const [countryCode, setCountryCode] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [selectedType, setSelectedType] = useState<HolidayType | ''>('');

  // 페이지네이션
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // 갱신/삭제 파라미터
  const [refreshYear, setRefreshYear] = useState<number | ''>(2025);
  const [refreshCountry, setRefreshCountry] = useState('');

  const searchHolidays = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const params: SearchParams = {
        countryCode: countryCode || undefined,
        from: fromDate || undefined,
        to: toDate || undefined,
        type: (selectedType || undefined) as HolidayType | undefined,
        page: currentPage,
        size: 20
      };

      const response = await holidayApi.searchHolidays(params);
      
      console.log('API Response:', response);
      console.log('Content:', response.content);
      console.log('Content length:', response.content?.length);
      
      // 백엔드 실제 응답 구조에 맞게 수정
      const content = response.content || [];
      const totalPages = response.page?.totalPages || 0;
      const totalElements = response.page?.totalElements || 0;
      
      console.log('Setting holidays:', content);
      console.log('Total pages:', totalPages);
      console.log('Total elements:', totalElements);
      
      setHolidays(content);
      setTotalPages(totalPages);
      setTotalElements(totalElements);
      
      console.log('State updated');
    } catch (err) {
      setError('공휴일 조회에 실패했습니다.');
      console.error('Error:', err);
      setHolidays([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    if (!refreshYear || !refreshCountry) {
      alert('연도와 국가 코드를 입력해주세요.');
      return;
    }
    
    if (!window.confirm(`${refreshYear}년 ${refreshCountry} 공휴일 데이터를 갱신하시겠습니까?`)) {
      return;
    }

    try {
      await holidayApi.refreshHolidays(Number(refreshYear), refreshCountry);
      alert('갱신이 완료되었습니다.');
      if (holidays.length > 0) {
        searchHolidays();
      }
    } catch (err) {
      alert('갱신에 실패했습니다.');
      console.error(err);
    }
  };

  const handleDelete = async () => {
    if (!refreshYear || !refreshCountry) {
      alert('연도와 국가 코드를 입력해주세요.');
      return;
    }
    
    if (!window.confirm(`${refreshYear}년 ${refreshCountry} 공휴일 데이터를 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await holidayApi.deleteHolidays(Number(refreshYear), refreshCountry);
      alert('삭제가 완료되었습니다.');
      if (holidays.length > 0) {
        searchHolidays();
      }
    } catch (err) {
      alert('삭제에 실패했습니다.');
      console.error(err);
    }
  };

  useEffect(() => {
    // 페이지 변경 시에만 조회 (초기 로드 제외)
    if (currentPage > 0) {
      searchHolidays();
    }
  }, [currentPage]); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <div className="app">
      <header className="header">
        <h1>Holiday Keeper</h1>
        <p>세계 공휴일 관리 시스템</p>
      </header>

      <main className="main">
        {/* 검색 섹션 */}
        <section className="search-section">
          <h2>🔍 공휴일 검색</h2>
          <div className="search-form">
            <div className="form-row">
              <div className="form-group">
                <label>국가 코드</label>
                <input
                  type="text"
                  placeholder="예: KR, US"
                  value={countryCode}
                  onChange={(e) => setCountryCode(e.target.value)}
                />
              </div>
              
              <div className="form-group">
                <label>시작일</label>
                <input
                  type="date"
                  value={fromDate}
                  onChange={(e) => setFromDate(e.target.value)}
                />
              </div>
              
              <div className="form-group">
                <label>종료일</label>
                <input
                  type="date"
                  value={toDate}
                  onChange={(e) => setToDate(e.target.value)}
                />
              </div>
              
              <div className="form-group">
                <label>타입</label>
                <select
                  value={selectedType}
                  onChange={(e) => setSelectedType(e.target.value as HolidayType | '')}
                >
                  <option value="">전체</option>
                  {Object.values(HolidayType).map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>
            </div>

            <button 
              className="btn btn-primary"
              onClick={() => {
                setCurrentPage(0);
                searchHolidays();
              }}
              disabled={loading}
            >
              {loading ? '검색 중...' : '검색'}
            </button>
          </div>
        </section>

        {/* 데이터 관리 섹션 */}
        <section className="management-section">
          <h2>⚙️ 데이터 관리</h2>
          <div className="management-form">
            <div className="form-row">
              <div className="form-group">
                <label>연도</label>
                <input
                  type="number"
                  placeholder="예: 2025"
                  value={refreshYear}
                  onChange={(e) => setRefreshYear(e.target.value ? Number(e.target.value) : '')}
                />
              </div>
              
              <div className="form-group">
                <label>국가 코드</label>
                <input
                  type="text"
                  placeholder="예: KR, US"
                  value={refreshCountry}
                  onChange={(e) => setRefreshCountry(e.target.value)}
                />
              </div>
            </div>

            <div className="button-group">
              <button className="btn btn-success" onClick={handleRefresh}>
                🔄 갱신 (Refresh)
              </button>
              <button className="btn btn-danger" onClick={handleDelete}>
                🗑️ 삭제
              </button>
            </div>
          </div>
        </section>

        {/* 결과 표시 */}
        <section className="results-section">
          <div className="results-header">
            <h2>📋 검색 결과</h2>
            {!loading && (
              <span className="results-count">
                총 {totalElements}개
              </span>
            )}
          </div>

          {loading ? (
            <div className="loading">검색 중...</div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : holidays.length === 0 ? (
            <div className="empty-message">검색 결과가 없습니다.</div>
          ) : (
            <>
              <div className="table-container">
                <table className="holiday-table">
                  <thead>
                    <tr>
                      <th>날짜</th>
                      <th>국가</th>
                      <th>현지명</th>
                      <th>영문명</th>
                      <th>타입</th>
                      <th>고정</th>
                      <th>전국</th>
                    </tr>
                  </thead>
                  <tbody>
                    {holidays.map((holiday, index) => (
                      <tr key={`${holiday.countryCode}-${holiday.date}-${index}`}>
                        <td>{holiday.date}</td>
                        <td>{holiday.countryCode}</td>
                        <td>{holiday.localName}</td>
                        <td>{holiday.englishName}</td>
                        <td>
                          <div className="types-container">
                            {holiday.types && holiday.types.length > 0 ? (
                              holiday.types.map((type, idx) => (
                                <span key={`${type}-${idx}`} className="type-badge">
                                  {type}
                                </span>
                              ))
                            ) : (
                              <span>-</span>
                            )}
                          </div>
                        </td>
                        <td>{holiday.fixed ? '✓' : '-'}</td>
                        <td>{holiday.globalHoliday ? '✓' : '-'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* 페이지네이션 */}
              <div className="pagination">
                <button
                  className="btn btn-secondary"
                  onClick={() => setCurrentPage(prev => prev - 1)}
                  disabled={currentPage === 0}
                >
                  이전
                </button>
                
                <span className="page-info">
                  {currentPage + 1} / {totalPages}
                </span>
                
                <button
                  className="btn btn-secondary"
                  onClick={() => setCurrentPage(prev => prev + 1)}
                  disabled={currentPage >= totalPages - 1}
                >
                  다음
                </button>
              </div>
            </>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;