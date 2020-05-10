package com.kk.community.util;
import com.github.binarywang.java.emoji.EmojiConverter;
/**
 * @author : K k
 * @date : 14:01 2020/5/1
 * 表情处理类
 */

public final class EmojiUtil {

    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    /**
     * 将emojiStr转为 转回带有表情的字符
     * @param emojiStr
     * @return
     */
    public static String emojiConverterUnicodeStr(String emojiStr){
        String result = emojiConverter.toUnicode(emojiStr);
        return result;
    }

    /**
     * 带有表情的字符串转换为编码 存入数据库
     * @param str
     * @return
     */
    public static String emojiConverterToAlias(String str){
        String result=emojiConverter.toAlias(str);
        return result;
    }


}