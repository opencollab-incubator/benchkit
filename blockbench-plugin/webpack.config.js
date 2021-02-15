const path = require('path')

module.exports = {
    mode: 'production',
    devtool: false,
    target: 'node',
    entry: 'index.ts',
    module: {
        rules: [
          {
            test: /\.tsx?$/,
            use: 'ts-loader',
            exclude: /node_modules/,
          },
          {
            test: /\.html$/,
            use: 'raw-loader'
          }
        ],
      },
      resolve: {
        extensions: ['.ts', '.js'],
        symlinks: false,
        modules: [path.resolve(__dirname), path.resolve('node_modules')]
      },
    output: {
        filename: 'benchkit.js',
        path: path.resolve(__dirname, 'build')
    }
}