package dev.utils;

import dev.models.Cart;
import dev.models.User;
import dev.services.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Base64;

public class CookieUtil {

    public static final String CART_COOKIE_NAME = "cart_data";
    public static final String CART_OBJECT_COOKIE_NAME = "cart_obj";
    public static final String USER_COOKIE_NAME = "user_cookie";
    public static final String USER_OBJECT_COOKIE_NAME = "user_obj";
    public static final int DEFAULT_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    public static String getCookieValue(Cookie[] cookies, String cookieName) {
        String cookieValue = "";
        if (cookies != null && cookieName != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        return cookieValue;
    }

    public static Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies != null && cookieName != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        if (response == null || name == null) {
            return;
        }
        Cookie cookie = new Cookie(name, value != null ? value : "");
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        if (response == null || name == null) {
            return;
        }
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // ==========================================
    // GENERIC OBJECT COOKIE SYNC (Serialization)
    // ==========================================

    public static void setObjectCookie(HttpServletResponse response, String name, Serializable object, int maxAge) {
        if (response == null || name == null) {
            return;
        }
        if (object == null) {
            deleteCookie(response, name);
            return;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
            String base64 = Base64.getUrlEncoder().encodeToString(baos.toByteArray());
            addCookie(response, name, base64, maxAge);
        } catch (Exception e) {
            System.err.println("[CookieUtil] Error serializing object cookie '" + name + "': " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectCookie(Cookie[] cookies, String name, Class<T> clazz) {
        String value = getCookieValue(cookies, name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value.trim());
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bais) {
                     @Override
                     protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                         ClassLoader cl = Thread.currentThread().getContextClassLoader();
                         if (cl != null) {
                             try {
                                 return Class.forName(desc.getName(), false, cl);
                             } catch (ClassNotFoundException ignored) {
                             }
                         }
                         return super.resolveClass(desc);
                     }
                 }) {
                Object obj = ois.readObject();
                if (clazz.isInstance(obj)) {
                    return (T) obj;
                }
            }
        } catch (Exception e) {
            System.err.println("[CookieUtil] Error deserializing object cookie '" + name + "': " + e.getMessage());
        }
        return null;
    }

    // ==========================================
    // USER OBJECT COOKIE SYNC
    // ==========================================

    public static void syncUser(HttpServletRequest request, HttpServletResponse response, User user) {
        if (request != null) {
            request.getSession().setAttribute("user", user);
        }
        if (response != null && user != null) {
            setObjectCookie(response, USER_OBJECT_COOKIE_NAME, user, DEFAULT_COOKIE_MAX_AGE);
            addCookie(response, USER_COOKIE_NAME, user.getEmail(), DEFAULT_COOKIE_MAX_AGE);
        }
    }

    public static User getSyncedUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession();
        User user = null;
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            user = (User) sessionUser;
        } else if (sessionUser != null) {
            session.removeAttribute("user");
        }

        if (user == null) {
            user = getObjectCookie(request.getCookies(), USER_OBJECT_COOKIE_NAME, User.class);
            if (user != null) {
                session.setAttribute("user", user);
            }
        }
        return user;
    }

    // ==========================================
    // CART OBJECT COOKIE SYNC
    // ==========================================

    public static void syncCart(HttpServletRequest request, HttpServletResponse response, Cart cart) {
        if (request != null) {
            request.getSession().setAttribute("cart", cart);
        }
        if (response != null) {
            if (cart == null || cart.getItems().isEmpty()) {
                deleteCookie(response, CART_COOKIE_NAME);
                deleteCookie(response, CART_OBJECT_COOKIE_NAME);
            } else {
                setObjectCookie(response, CART_OBJECT_COOKIE_NAME, cart, DEFAULT_COOKIE_MAX_AGE);
            }
        }
    }

    public static Cart getSyncedCart(HttpServletRequest request, ProductService productService) {
        if (request == null) {
            return new Cart();
        }
        HttpSession session = request.getSession();
        Cart cart = null;
        Object sessionCart = session.getAttribute("cart");
        if (sessionCart instanceof Cart) {
            cart = (Cart) sessionCart;
        } else if (sessionCart != null) {
            session.removeAttribute("cart");
        }

        if (cart == null) {
            cart = getObjectCookie(request.getCookies(), CART_OBJECT_COOKIE_NAME, Cart.class);
            if (cart == null) {
                cart = new Cart();
            }
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
