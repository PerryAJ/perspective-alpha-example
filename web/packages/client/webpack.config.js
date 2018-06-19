const webpack = require('webpack'),
    path = require('path'),
    fs = require('fs'),
    { CheckerPlugin, TsConfigPathsPlugin } = require('awesome-typescript-loader');

const WebpackOnBuildPlugin = require('on-build-webpack');

const LibName = "RadComponents";

// function that copies the result of the webpack from the dist/ folder into the gateway resources folder.  Used
// in a post-build step so that our web assets are packed into our jar when the module is built.
function copyToResources() {
    const resourceFolder = path.resolve(__dirname, '../../..', 'gateway/src/main/resources/mounted/js/');
    const toCopy = path.resolve(__dirname, "dist/", `${LibName}.js`);
    const resourcePath = path.resolve(resourceFolder, `${LibName}.js`);


    // if the desired folder doesn't exist, create it
    if (!fs.existsSync(resourceFolder)){
        fs.mkdirSync(resourceFolder)
    }


    try {
        console.log(`copying ${toCopy}...`);
        fs.access(toCopy, fs.constants.R_OK, (err) => {

            if (!err) {
                fs.createReadStream(toCopy)
                    .pipe(fs.createWriteStream(resourcePath));
            } else {
                console.log(`Error when attempting to copy ${toCopy} into ${resourcePath}`)
            }
        });
    } catch (err) {
        console.log(err);
    }
}


var config = {

    // define our entry point, from which we build our source tree for bundling
    entry: {
        RadComponents:  path.join(__dirname, "./typescript/rad-client-components.ts"),
    },

    output: {
        library: [LibName],  // name as it will be accessible by on the webpack when linked as a script
        path: path.join(__dirname, "dist"),
        filename: `${LibName}.js`,
        libraryTarget: "umd",
        umdNamedDefine: true
    },

    // Enable sourcemaps for debugging webpack's output.
    devtool: "eval-source-map",

    resolve: {
        extensions: [".jsx", ".js", ".ts", ".tsx", ".d.ts"],
        modules: [
            path.resolve(__dirname, "../../node_modules"),  // look at the local as well as shared node modules when resolving dependencies
        ],
    },

    module: {
        rules: [
            // use the typescript loader for ts and tsx files, but don't bother looking in node modules
            {
                test: /\.tsx?$/,
                loader: 'awesome-typescript-loader',
                exclude: /node_modules/,
            }
        ],
    },
    plugins: [
        new WebpackOnBuildPlugin(function(stats) {
            copyToResources();
        }),
        new CheckerPlugin(),  // checks the typescript for correctness
        new TsConfigPathsPlugin(), // helps wire up the tsconfig paths we use so webpack resolves files correctly.
    ],

    // IMPORTANT -- this tells the webpack build tooling "don't include these things as part of the webpack bundle".
    // They are 'provided' 'externally' via perspective/ignition at runtime, and we don't want multiple copies in the
    // browser.  Any libraries used that are also used in perspective should be excluded.
    externals: {
        "react": "React",
        "react-dom": "ReactDOM",
        "mobx": "mobx",
        "mobx-react": "mobxReact",
        "@inductiveautomation/perspective-client": "PerspectiveClient"
    },
};


module.exports = () => config;
