import configureStyle from "./configure/styles.css";

export const styles = configureStyle;

type HTMLStyleElementIE8 = HTMLStyleElement & {
    styleSheet?: CSSStyleDeclaration
}

/**
 * Creates a style element containing all the styles from the screens
 * and adds it to the page HTML.
 */
export function loadStyles() {
    // Create the style element
    const styleNode: HTMLStyleElementIE8 = document.createElement("style");
    styleNode.type = "text/css";

    if (styleNode.styleSheet) {
        // This is required for IE8 and below
        styleNode.styleSheet.cssText = styles;
    } else {
        styleNode.appendChild(document.createTextNode(styles));
    }
    document.getElementsByTagName("head")[0].appendChild(styleNode);
}