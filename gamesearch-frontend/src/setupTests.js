import '@testing-library/jest-dom';

// Mock window.scrollTo since it's not implemented in JSDOM
window.scrollTo = jest.fn();
