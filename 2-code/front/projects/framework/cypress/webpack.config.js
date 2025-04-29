const path = require("path");

module.exports = {
    resolve: {
        extensions: [".ts", ".js"]
    },
    node: {},
    module: {
        rules: [
            {
                test: /\.ts$/,
                exclude: [/node_modules/],
                use: [
                    {
                        loader: "ts-loader"
                    }
                ]
            }
        ]
    }
};