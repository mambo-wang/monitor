package com.virtual.cloud.om.agent.service.logs;

import com.virtual.cloud.om.sdk.dto.LogLine;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineParserUtil {

    /* 正则表达式模式说明:
       匹配时间: ([^\[\]]*)
       匹配日志级别,线程名称等被中括号包裹的字符串: \[([^\[\]]*)\]
       精确匹配字符串start或ended: (start|ended,)
       匹配剩余字符:(.*)
     */

    // 日志匹配模式表达式. 用括号表示捕获匹配内容, 在后面可以时候用matcher.group(idx)获取捕获的字符串内容
    private static final String pattenExp = "([^\\[\\]]*)\\[([^\\[\\]]*)\\]\\[([^\\[\\]]*)\\]\\[([^\\[\\]]*)\\]\\[([^\\[\\]]*)\\]([^\\[\\]]*)\\s+\\[([^\\[\\]]*)\\]\\s+-\\s+([^\\[\\]]*)(.*)";

    private static Pattern pattern ;

    static {
        pattern = Pattern.compile(pattenExp);
    }

    /**
     * @Description: 解析单行日志
     * @param line 日志
     * @return: LogLine
     * @author: zongf
     * @time: 2019-04-02 11:08:37
     */
    public static Optional<LogLine> parserLine(String line, String logPattern) {

        Pattern finallyPattern = pattern;
        if(StringUtils.isNotEmpty(logPattern) && !StringUtils.equals(logPattern, pattenExp)){
            finallyPattern = Pattern.compile(logPattern);
        }
        // 匹配单行日志
        Matcher matcher = finallyPattern.matcher(line);
        boolean isFind = matcher.find();

        if (isFind) {
            // 匹配成功, 则创建日志模型
            LogLine logLine = new LogLine();

            // 赋值每个字段
            logLine.setTime(matcher.group(1));
            logLine.setLevel(matcher.group(2));
            logLine.setThread(matcher.group(3));
            logLine.setRequestUuid(matcher.group(4));
            logLine.setRequestIp(matcher.group(5));
            logLine.setMethod(matcher.group(6));
            logLine.setLine(matcher.group(7));
            logLine.setMessage(matcher.group(8));
            return Optional.of(logLine);
        }

        //匹配失败, 返回空
        return Optional.empty();
    }

    public static void main(String[] args) {
        String log = "2022-05-21 17:21:00.013[WARN ][quartzScheduler_Worker-3][][]org.hibernate.orm.deprecation.createCriteria [1874] - HHH90000022: Hibernate's legacy org.hibernate.Criteria API is deprecated; use the JPA javax.persistence.criteria.CriteriaQuery instead";
        Optional<LogLine> logLine = parserLine(log, null);
        System.out.println(logLine.get());
    }
}
