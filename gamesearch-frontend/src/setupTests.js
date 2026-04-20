// jest-dom adds custom jest matchers for asserting on DOM nodes.
import '@testing-library/jest-dom';

// Mock window.scrollTo to prevent jsdom errors with framer-motion
window.scrollTo = jest.fn();
