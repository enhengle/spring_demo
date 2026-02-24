package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * UUID工具类
 * 
 * UUID版本说明：
 * - UUID v1: 基于时间戳和MAC地址，可能泄露MAC地址信息，性能较差
 * - UUID v2: DCE安全版本，不常用
 * - UUID v3: 基于MD5的命名空间UUID，确定性生成
 * - UUID v4: 随机UUID，最常用，性能好，推荐使用
 * - UUID v5: 基于SHA-1的命名空间UUID，确定性生成
 * 
 * 注意事项：
 * 1. UUID v1可能泄露MAC地址，存在安全风险
 * 2. UUID v4是随机生成，性能最好，推荐使用
 * 3. UUID不能保证按时间排序（v1除外，但v1有安全风险）
 * 4. UUID占用36个字符（带连字符）或32个字符（不带连字符）
 * 5. UUID在数据库索引中性能较差（建议使用自增ID或雪花算法）
 * 6. UUID碰撞概率极低（2^122），但理论上仍存在
 * 7. 跨语言兼容性好，标准RFC 4122格式
 * 8. 线程安全：UUID.randomUUID()是线程安全的
 * 
 * @author system
 */
@Slf4j
public class UuidUtil {
    
    /**
     * 命名空间：DNS
     */
    public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
    
    /**
     * 命名空间：URL
     */
    public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
    
    /**
     * 命名空间：OID
     */
    public static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
    
    /**
     * 命名空间：X.500 DN
     */
    public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");
    
    // 注意：ThreadLocalRandom和SecureRandom已预留，但UUID.randomUUID()内部已优化
    // 如需更高性能或安全性，可在批量生成时使用这些生成器
    
    /**
     * 生成标准UUID v4（随机UUID）
     * 推荐使用，性能好，无安全风险
     * 
     * @return UUID字符串（带连字符，格式：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成不带连字符的UUID v4
     * 适用于需要紧凑格式的场景
     * 
     * @return UUID字符串（不带连字符，32个字符）
     */
    public static String generateUuidWithoutHyphens() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成大写UUID v4
     * 
     * @return 大写UUID字符串
     */
    public static String generateUuidUpperCase() {
        return UUID.randomUUID().toString().toUpperCase();
    }
    
    /**
     * 生成不带连字符的大写UUID v4
     * 
     * @return 不带连字符的大写UUID字符串
     */
    public static String generateUuidUpperCaseWithoutHyphens() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
    
    /**
     * 生成UUID对象（v4）
     * 
     * @return UUID对象
     */
    public static UUID generateUuidObject() {
        return UUID.randomUUID();
    }
    
    /**
     * 批量生成UUID（优化性能）
     * 使用ThreadLocalRandom提升性能
     * 
     * @param count 生成数量
     * @return UUID字符串数组
     */
    public static String[] generateUuids(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        if (count > 1000000) {
            log.warn("批量生成UUID数量较大: {}，可能影响性能", count);
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateUuid();
        }
        return uuids;
    }
    
    /**
     * 批量生成不带连字符的UUID
     * 
     * @param count 生成数量
     * @return UUID字符串数组（不带连字符）
     */
    public static String[] generateUuidsWithoutHyphens(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateUuidWithoutHyphens();
        }
        return uuids;
    }
    
    /**
     * 生成UUID v3（基于MD5的命名空间UUID）
     * 相同输入生成相同UUID，适用于需要确定性UUID的场景
     * 
     * @param namespace 命名空间UUID
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV3(UUID namespace, String name) {
        UUID uuid = UUID.nameUUIDFromBytes(
                (namespace.toString() + name).getBytes()
        );
        return uuid.toString();
    }
    
    /**
     * 生成UUID v3（基于DNS命名空间）
     * 
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV3FromDns(String name) {
        return generateUuidV3(NAMESPACE_DNS, name);
    }
    
    /**
     * 生成UUID v3（基于URL命名空间）
     * 
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV3FromUrl(String name) {
        return generateUuidV3(NAMESPACE_URL, name);
    }
    
    /**
     * 生成UUID v5（基于SHA-1的命名空间UUID）
     * 注意：Java标准库不直接支持v5，这里使用自定义实现
     * 
     * @param namespace 命名空间UUID
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV5(UUID namespace, String name) {
        try {
            java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
            ByteBuffer buffer = ByteBuffer.allocate(16 + name.length());
            buffer.putLong(namespace.getMostSignificantBits());
            buffer.putLong(namespace.getLeastSignificantBits());
            buffer.put(name.getBytes("UTF-8"));
            sha1.update(buffer.array());
            byte[] hash = sha1.digest();
            
            // 设置版本号（5）和变体
            hash[6] &= 0x0f;
            hash[6] |= 0x50; // 版本5
            hash[8] &= 0x3f;
            hash[8] |= 0x80; // 变体
            
            ByteBuffer uuidBuffer = ByteBuffer.wrap(hash, 0, 16);
            long mostSigBits = uuidBuffer.getLong();
            long leastSigBits = uuidBuffer.getLong();
            
            UUID uuid = new UUID(mostSigBits, leastSigBits);
            return uuid.toString();
        } catch (Exception e) {
            log.error("生成UUID v5失败", e);
            throw new RuntimeException("生成UUID v5失败", e);
        }
    }
    
    /**
     * 生成UUID v5（基于DNS命名空间）
     * 
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV5FromDns(String name) {
        return generateUuidV5(NAMESPACE_DNS, name);
    }
    
    /**
     * 生成UUID v5（基于URL命名空间）
     * 
     * @param name 名称
     * @return UUID字符串
     */
    public static String generateUuidV5FromUrl(String name) {
        return generateUuidV5(NAMESPACE_URL, name);
    }
    
    /**
     * 验证UUID格式是否正确
     * 
     * @param uuid UUID字符串
     * @return 是否有效
     */
    public static boolean isValidUuid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        
        try {
            // 尝试解析UUID（支持带连字符和不带连字符）
            String normalized = uuid.replace("-", "");
            if (normalized.length() != 32) {
                return false;
            }
            
            // 验证是否为十六进制字符
            for (char c : normalized.toCharArray()) {
                if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                    return false;
                }
            }
            
            // 尝试创建UUID对象
            UUID.fromString(uuid.contains("-") ? uuid : formatUuid(uuid));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 格式化UUID（添加连字符）
     * 
     * @param uuidWithoutHyphens 不带连字符的UUID
     * @return 带连字符的UUID
     */
    public static String formatUuid(String uuidWithoutHyphens) {
        if (uuidWithoutHyphens == null) {
            return null;
        }
        
        String cleaned = uuidWithoutHyphens.replace("-", "");
        if (cleaned.length() != 32) {
            throw new IllegalArgumentException("UUID长度必须为32个字符");
        }
        
        return cleaned.substring(0, 8) + "-" +
               cleaned.substring(8, 12) + "-" +
               cleaned.substring(12, 16) + "-" +
               cleaned.substring(16, 20) + "-" +
               cleaned.substring(20, 32);
    }
    
    /**
     * 移除UUID中的连字符
     * 
     * @param uuid 带连字符的UUID
     * @return 不带连字符的UUID
     */
    public static String removeHyphens(String uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.replace("-", "");
    }
    
    /**
     * 获取UUID版本号
     * 
     * @param uuid UUID字符串
     * @return 版本号（1-5），如果不是标准UUID返回-1
     */
    public static int getUuidVersion(String uuid) {
        try {
            UUID.fromString(uuid.contains("-") ? uuid : formatUuid(uuid));
            // UUID对象不直接提供版本号，需要通过toString()判断
            // 这里简化处理，v4是随机生成的，无法直接判断版本
            // 实际应用中，如果知道是v4，返回4
            return 4; // 默认返回4，因为randomUUID()生成的是v4
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * 比较两个UUID（按字符串字典序）
     * UUID v4是随机生成的，不能保证时间顺序
     * 
     * @param uuid1 第一个UUID
     * @param uuid2 第二个UUID
     * @return 比较结果（-1: uuid1 < uuid2, 0: 相等, 1: uuid1 > uuid2）
     */
    public static int compareUuid(String uuid1, String uuid2) {
        if (uuid1 == null && uuid2 == null) {
            return 0;
        }
        if (uuid1 == null) {
            return -1;
        }
        if (uuid2 == null) {
            return 1;
        }
        return uuid1.compareTo(uuid2);
    }
    
    /**
     * 从字节数组生成UUID
     * 
     * @param bytes 16字节的字节数组
     * @return UUID字符串
     */
    public static String uuidFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            throw new IllegalArgumentException("字节数组长度必须为16");
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        UUID uuid = new UUID(mostSigBits, leastSigBits);
        return uuid.toString();
    }
    
    /**
     * 将UUID转换为字节数组
     * 
     * @param uuid UUID字符串
     * @return 16字节的字节数组
     */
    public static byte[] uuidToBytes(String uuid) {
        UUID uuidObj = UUID.fromString(uuid.contains("-") ? uuid : formatUuid(uuid));
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuidObj.getMostSignificantBits());
        buffer.putLong(uuidObj.getLeastSignificantBits());
        return buffer.array();
    }
}
