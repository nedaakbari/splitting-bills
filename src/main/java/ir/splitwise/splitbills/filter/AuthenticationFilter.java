package ir.splitwise.splitbills.filter;

import io.jsonwebtoken.Claims;
import ir.splitwise.splitbills.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authorization = request.getHeader("Authorization");//todo must be from cookie
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);//todo
            return;
        }
        String token = authorization.substring(7);
        String subject = (String) jwtService.getClaim(token, "subject");
        if (subject!=null){

        }


    }
}
