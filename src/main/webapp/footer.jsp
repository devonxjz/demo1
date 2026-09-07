<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<footer class="site-footer">
    <p>&copy; <%= java.time.Year.now().getValue() %> Devonxjz. All rights reserved.</p>
</footer>

<!-- Tự động trở về trang chủ khi người dùng nhấn Refresh (F5 / Reload) trên web -->
<script>
    (function() {
        try {
            var path = window.location.pathname;
            var ctx = "${pageContext.request.contextPath}";
            var isHome = (path === ctx) || (path === (ctx + "/")) || (path === (ctx + "/index.jsp")) || path.endsWith("/index.jsp");
            
            if (!isHome && window.performance) {
                var isReload = false;
                var navEntries = performance.getEntriesByType("navigation");
                if (navEntries && navEntries.length > 0) {
                    isReload = (navEntries[0].type === "reload");
                } else if (performance.navigation) {
                    isReload = (performance.navigation.type === 1);
                }
                
                if (isReload) {
                    window.location.replace(ctx + "/index.jsp");
                }
            }
        } catch (e) {
            // Ignored
        }
    })();
</script>