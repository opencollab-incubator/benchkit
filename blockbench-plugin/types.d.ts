/// <reference types="blockbench-types" />

/**
 * This allows us to import html files without typescript complaining.
 */
declare module '*.html' {
    const content: string;
    export default content;
}

/**
 * This allows us to import css files without typescript complaining.
 */
declare module '*.css' {
    const content: string;
    export default content;
}