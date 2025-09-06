package com.example.travelapp.security;


/**
 * Utility to enforce permission checks. Permissions for each service method are
 * centrally configured in {@link PermissionRegistry}. Callers only need to
 * invoke {@code PermissionGuard.require()} and the guard will determine the
 * required permission based on the calling class and method.
 */
public class PermissionGuard {

        /**
         * Explicitly require a particular permission code. This method remains for
         * situations where the permission needs to be checked outside of the
         * registry-based mechanism.
         */
        public static void require(String permissionCode) {
                if (!SecurityContext.hasPermission(permissionCode)) {
                        throw new SecurityException("Từ chối quyền truy cập: " + permissionCode);
                }
        }

        /**
         * Look up the required permission of the calling method from the
         * {@link PermissionRegistry} and enforce it. If no permission is configured
         * for the method, the call is allowed.
         */
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