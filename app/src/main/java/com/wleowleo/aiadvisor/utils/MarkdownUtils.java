package com.wleowleo.aiadvisor.utils;

import android.text.Html;
import android.text.Spanned;

public class MarkdownUtils {
    
    /**
     * Converts basic markdown text to HTML and returns a Spanned object for TextView
     */
    public static Spanned markdownToSpanned(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Html.fromHtml("", Html.FROM_HTML_MODE_COMPACT);
        }
        
        String html = markdownToHtml(markdown);
        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
    }
    
    /**
     * Converts basic markdown patterns to HTML
     */
    private static String markdownToHtml(String markdown) {
        String html = markdown;
        
        // Convert headers (# ## ###)
        html = html.replaceAll("(?m)^### (.+)$", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^## (.+)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^# (.+)$", "<h1>$1</h1>");
        
        // Convert bold (**text** or __text__)
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("__(.+?)__", "<b>$1</b>");
        
        // Convert italic (*text* or _text_)
        html = html.replaceAll("(?<!\\*)\\*([^*]+?)\\*(?!\\*)", "<i>$1</i>");
        html = html.replaceAll("(?<!_)_([^_]+?)_(?!_)", "<i>$1</i>");
        
        // Convert code blocks (```code```)
        html = html.replaceAll("```([\\s\\S]*?)```", "<pre><code>$1</code></pre>");
        
        // Convert inline code (`code`)
        html = html.replaceAll("`([^`]+?)`", "<code>$1</code>");
        
        // Convert bullet points (- item or * item)
        html = html.replaceAll("(?m)^[*-] (.+)$", "<li>$1</li>");
        
        // Wrap consecutive list items in <ul> tags
        html = html.replaceAll("(<li>.*</li>(?:\\s*<li>.*</li>)*)", "<ul>$1</ul>");
        
        // Convert numbered lists (1. item)
        html = html.replaceAll("(?m)^\\d+\\. (.+)$", "<li>$1</li>");
        
        // Convert line breaks to <br>
        html = html.replaceAll("\\n\\n", "<br><br>");
        html = html.replaceAll("\\n", "<br>");
        
        return html;
    }
}
