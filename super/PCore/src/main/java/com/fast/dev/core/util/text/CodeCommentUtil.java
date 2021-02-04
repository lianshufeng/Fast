package com.fast.dev.core.util.text;

import com.fast.dev.core.util.path.PathUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.SimpleName;
import lombok.SneakyThrows;
import org.codehaus.groovy.tools.StringHelper;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代码注释工具类
 */
public class CodeCommentUtil {

    private final static String ParamText = "@param";


    @SneakyThrows
    public static String readFieldComment(Field field) {
        File sourceFile = toSourceFile(field.getDeclaringClass());
        if (sourceFile == null) {
            return null;
        }
        //用于存放方法上面的注释
        String[] fieldComments = new String[1];
        //取方法名
        final String fieldName = field.getName();
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> result = javaParser.parse(sourceFile);
        result.getResult().ifPresent((it) -> {
            for (Comment comment : it.getAllContainedComments()) {
                comment.getCommentedNode().ifPresent((node) -> {

                    //取出方法注释
                    String[] commentText = new String[1];
                    node.getComment().ifPresent((comm) -> {
                        commentText[0] = comm.getContent();
                    });

                    //过滤节点类型，，取属性名的子节点
                    List<Node> codeNodes = node.getChildNodes().stream().filter((childNode) -> {
                        return childNode instanceof VariableDeclarator;
                    }).collect(Collectors.toList());
                    if (codeNodes == null || codeNodes.size() == 0) {
                        return;
                    }
                    //取出代码中的方法名
                    VariableDeclarator variableDeclarator = (VariableDeclarator) codeNodes.get(0);
                    //匹配方法名相同的
                    if (fieldName.equals(variableDeclarator.getName().getIdentifier())) {
                        fieldComments[0] = commentText[0];
                    }

                });

            }
        });
        return StringUtils.hasText(fieldComments[0]) ? formatComment(fieldComments[0]) : null;
    }


    @SneakyThrows
    public static String readMethodComment(Method method) {
        return readMethodComment(method, null);
    }

    /**
     * 读取方法上面的注释，并格式化（注：须在开发环境下使用)
     *
     * @return
     */
    @SneakyThrows
    public static String readMethodComment(Method method, Map<String, String> paramCommentMap) {
        File sourceFile = toSourceFile(method.getDeclaringClass());
        if (sourceFile == null) {
            return null;
        }
        //用于存放方法上面的注释
        String[] methodComments = new String[1];
        //取方法名
        final String methodName = method.getName();
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> result = javaParser.parse(sourceFile);
        result.getResult().ifPresent((it) -> {
            for (Comment comment : it.getAllContainedComments()) {
                comment.getCommentedNode().ifPresent((node) -> {

                    //取出方法注释
                    String[] commentText = new String[1];
                    node.getComment().ifPresent((comm) -> {
                        commentText[0] = comm.getContent();
                    });

                    //没有注释的每一个方法
                    Node noCommentNode = node.removeComment();

                    //过滤节点类型，，取方法名的子节点
                    List<Node> codeNodes = noCommentNode.getChildNodes().stream().filter((childNode) -> {
                        return childNode instanceof SimpleName;
                    }).collect(Collectors.toList());
                    if (codeNodes == null || codeNodes.size() == 0) {
                        return;
                    }

                    //取出代码中的方法名
                    SimpleName simpleName = (SimpleName) codeNodes.get(0);


                    //匹配方法名相同的
                    if (methodName.equals(simpleName.getIdentifier())) {
                        methodComments[0] = commentText[0];
                    }

                });

            }
        });

        return StringUtils.hasText(methodComments[0]) ? formatComment(methodComments[0], paramCommentMap) : null;
    }


    /**
     * 取出源码文件
     *
     * @param cls
     * @return
     */
    private static File toSourceFile(Class cls) {
        String classesPath = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
        String projectPath = PathUtil.getParent(PathUtil.getParent(classesPath, "/"), "/");
        String fileName = projectPath + "/src/main/java/" + TextUtil.join(cls.getPackageName().split("\\."), "/") + "/" + cls.getSimpleName();
        if (new File(fileName + ".java").exists()) {
            return new File(fileName + ".java");
        } else if (new File(fileName + ".groovy").exists()) {
            return new File(fileName + ".groovy");
        } else {
            return null;
        }
    }


    /**
     * 格式化注释文本
     *
     * @param textComment
     * @return
     */
    private static String formatComment(String textComment) {
        return formatComment(textComment, null);
    }

    /**
     * 格式化注释文本
     *
     * @param textComment
     * @return
     */
    private static String formatComment(String textComment, Map<String, String> paramCommentMap) {
        //处理一些符号
        String commentContent = textComment.replaceAll("//", "").replaceAll("\\*", "");


        StringBuffer sb = new StringBuffer();
        //过滤注解的注释
        for (String line : commentContent.split("\n")) {
            //取出左边所有的空格
            if (StringUtils.hasText(line)) {
                line = line.trim();
            }
            if (StringUtils.hasText(line) && line.indexOf("@") == -1) {
                sb.append(line);
            } else if (line.indexOf(ParamText) > -1 && line.length() > ParamText.length() + 1) {
                String paramCommentText = line.substring(ParamText.length() + 1, line.length());
                if (paramCommentMap != null) {
                    int at = paramCommentText.indexOf(" ");
                    if (at == -1) {
                        paramCommentMap.put(paramCommentText, "");
                    } else {
                        paramCommentMap.put(paramCommentText.substring(0, at), paramCommentText.substring(at + 1, paramCommentText.length()).trim());
                    }
                }
            }
        }

        //过滤空格与换行符
        return sb.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
    }

}
