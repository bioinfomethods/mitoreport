process.env.VUE_APP_VERSION = require('./package.json').version

module.exports = {
  publicPath: './',
  transpileDependencies: ['vuetify', 'keycloak-js'],
  configureWebpack: {
    devtool: 'source-map',
    module: {
      rules: [
        {
          test: /\.mjs$/,
          include: /node_modules/,
          type: 'javascript/auto',
        },
      ],
    },
  },
  chainWebpack: config => {
    if (process.env.NODE_ENV === 'test') {
      config.merge({
        devtool: 'cheap-module-eval-source-map',
      })
    }
  },
  devServer: {
    disableHostCheck: true,
  },
}
