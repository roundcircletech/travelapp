const http = require('http');

const advisories = [
    {
        id: 'adv-1',
        sourceCountry: 'US',
        destinationCountry: 'Russia',
        type: 'DIRECT_FLIGHT_BAN',
        severity: 'HIGH',
        message: 'Direct flights from US to Russia are suspended due to new regulations.',
        timestamp: new Date().toISOString()
    }
];

const server = http.createServer((req, res) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Content-Type', 'application/json');

    if (req.method === 'GET' && req.url === '/advisories') {
        res.writeHead(200);
        res.end(JSON.stringify(advisories));
    } else {
        res.writeHead(404);
        res.end(JSON.stringify({ error: 'Not Found' }));
    }
});

server.listen(3000, () => {
    console.log('Mock Advisory Server running on http://localhost:3000');
});
