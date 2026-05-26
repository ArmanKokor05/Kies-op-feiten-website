let API_BASE_URL: string;

const host = window.location.hostname;

if (host === 'localhost' || host === '127.0.0.1') {
  API_BASE_URL = 'http://localhost:8080';
} else if (host === '159.223.237.65') {
  API_BASE_URL = 'http://159.223.237.65:8080';
} else if (host === 'kiesopfeiten.com' || host === 'www.kiesopfeiten.com') {
  API_BASE_URL = 'https://api.kiesopfeiten.com';
} else {
  API_BASE_URL = 'http://localhost:8080';
}

export { API_BASE_URL };
