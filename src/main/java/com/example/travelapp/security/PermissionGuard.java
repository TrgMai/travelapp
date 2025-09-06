package com.example.travelapp.security;

public class PermissionGuard {
        public static void require(String permissionCode) {
                if (!SecurityContext.hasPermission(permissionCode)) {
                        throw new SecurityException("Từ chối quyền truy cập: " + permissionCode);
                }
        }

        public static void require() {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                if (stack.length < 3) {
                        return;
                }
                StackTraceElement caller = stack[2];
                String key = caller.getClassName() + "." + caller.getMethodName();
                String permissionCode = PermissionRegistry.PERMISSIONS.get(key);
                if (permissionCode != null) {
                        require(permissionCode);
                }
        }
}