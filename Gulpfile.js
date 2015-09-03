var gulp = require('gulp');

var jshint = require('gulp-jshint');
var wiredep = require('wiredep').stream;

var paths = {
    base: 'index.html',
    dist: 'src/main/webapp',
    fonts: 'lib/bootstrap/fonts'
};

gulp.task('wiredep', function () {
    require('wiredep')({src: paths.dist + '/' + paths.base, exclude: ['jquery', 'bootstrap.js']});
});

// The default task (called when you run `gulp` from cli)
gulp.task('default', ['lint', 'wiredep']);

gulp.task('lint', function () {
    gulp.src('js/**/*.js', {cwd: paths.dist})
        .pipe(jshint())
        .pipe(jshint.reporter("jshint-stylish"));
});
