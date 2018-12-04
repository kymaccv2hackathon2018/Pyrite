const PROXY_CONFIG = {

  '/api/*': {
    target: 'https://cartabandonment.sa-hackathon-10.cluster.extend.sap.cx/',
    logLevel: 'error',
    secure: true,
    changeOrigin: true,
    pathRewrite: {
      '^/api': ''
    }
  }
};

module.exports = PROXY_CONFIG;
