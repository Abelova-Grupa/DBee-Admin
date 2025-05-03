package com.abelovagrupa.dbeeadmin.util;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighter {

    public static final String[] SQL_KEYWORDS = {
        "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "GROUP", "HAVING", "ORDER", "INTO",
        "BY", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "ON", "AND", "OR", "NOT",
        "NULL", "IS", "BETWEEN", "LIKE", "IN", "EXISTS", "DISTINCT", "AS", "CREATE",
        "TABLE", "DROP", "ALTER", "ADD", "COLUMN", "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN",
        "REFERENCES", "CHECK", "DEFAULT", "INDEX", "VIEW", "TRIGGER", "PROCEDURE",
        "FUNCTION", "TRANSACTION", "COMMIT", "ROLLBACK", "SAVEPOINT", "GRANT", "REVOKE",
        "USER", "DATABASE", "USE", "EXPLAIN", "DESCRIBE", "SHOW", "LIMIT", "OFFSET", "FETCH",
        "WITH", "INNER JOIN", "OUTER JOIN", "CROSS JOIN", "UNION", "INTERSECT", "EXCEPT",
        "CASE", "WHEN", "THEN", "ELSE", "END", "IF", "LOOP", "WHILE", "RETURN",
        "SET", "VALUES", "COLUMN_NAME", "DATABASE_NAME", "SCHEMA", "ISNULL", "COALESCE",
        "CAST", "CONVERT", "TRIM", "SUBSTRING", "LEN", "ROUND", "AVG", "COUNT",
        "SUM", "MAX", "MIN", "LIMIT", "OFFSET", "ASC", "DESC", "INDEXES"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String NUMBER_PATTERN = "\\b\\d+\\b";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*[^*]*\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("STRING") != null ? "string" :
                        matcher.group("NUMBER") != null ? "number" :
                            matcher.group("COMMENT") != null ? "comment" :
                                null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
