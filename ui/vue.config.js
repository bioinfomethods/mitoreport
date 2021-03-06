process.env.VUE_APP_VERSION = require('./package.json').version

module.exports = {
  publicPath: './',
  transpileDependencies: ['vuetify'],
  configureWebpack: {
    devtool: 'source-map',
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
