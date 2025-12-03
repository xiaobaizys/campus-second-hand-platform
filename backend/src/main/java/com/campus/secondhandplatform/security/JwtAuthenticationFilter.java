package com.campus.secondhandplatform.security;

import com.campus.secondhandplatform.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwt = null;
        String requestURI = request.getRequestURI();
        logger.info("处理请求: {}", requestURI);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.getUsernameFromToken(jwt);
                logger.info("从JWT获取的用户ID: {}", userId);
            } catch (Exception e) {
                logger.error("无法获取JWT Token中的用户ID", e);
            }
        } else {
            logger.info("请求中没有Authorization头或格式不正确，Authorization头: {}", authorizationHeader);
        }

        if (userId != null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
                logger.info("从UserDetailsService获取的用户: {}, 用户名: {}", userDetails.getUsername(), userDetails.getUsername());

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("用户认证成功，设置认证信息: {}", usernamePasswordAuthenticationToken.getPrincipal());
                } else {
                    logger.info("JWT验证失败");
                }
            } catch (Exception e) {
                logger.error("用户认证过程中出错", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}