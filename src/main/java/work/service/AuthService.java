package work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import work.entity.User;
import work.entity.enums.Role;
import work.payload.ApiResponse;
import work.payload.AuthRegister;
import work.payload.Authlogin;
import work.repository.UserRepository;
import work.security.JwtProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Integer> codes =  new HashMap<>();
    private final MailService mailService;
    private final JwtProvider jwtProvider;


    public ApiResponse register(AuthRegister register){
        boolean exists = userRepository.existsByEmail(register.getEmail());
        if(!exists){
            User user = new User();
            user.setEmail(register.getEmail());
            user.setPassword(passwordEncoder.encode(register.getPassword()));
            user.setRole(Role.ROLE_USER);

            Random  random = new Random();
            int code = Math.abs(random.nextInt()%10000);
            codes.put(user.getEmail(),code);
            try {
                mailService.sendMail(user.getEmail(), "Ruyxatdan utish tasdiqlash kodi -> ",code);

            }catch (Exception e){
                new ApiResponse("Kod yuborishda xatolikyuzaga keldi qaytadan urinib kuring",
                        HttpStatus.INTERNAL_SERVER_ERROR, false, null);
            }
            users.put(user.getEmail(), user);
        }
        return new ApiResponse("email already exist", HttpStatus.BAD_REQUEST,false,null);
    }
    public ApiResponse check_code(String email, Integer code){
        if(users.containsKey(email)){
            Integer haveCod= codes.get(email);
            if(haveCod.equals(code)){
                codes.remove(email);
                User user = users.get(email);
                user.setEnabled(true);
                userRepository.save(user);
                return new ApiResponse("success checked code", HttpStatus.OK,true,null);
            }
            return new ApiResponse("Code incorrect",HttpStatus.BAD_REQUEST,false,null);
        }
        return new ApiResponse("email not found", HttpStatus.BAD_REQUEST,false,null);
    }

    public ApiResponse login(Authlogin authlogin){
        User user = users.get(authlogin.getEmail());
        if(user != null){
            if (passwordEncoder.matches(authlogin.getPassword(), user.getPassword())){
                String token = jwtProvider.generateToken(authlogin.getEmail());
                return new ApiResponse("success login", HttpStatus.OK,true,token);
            }
            return new ApiResponse("Password incorrect", HttpStatus.OK,true,null);
        }
        return new ApiResponse("Email not found", HttpStatus.NOT_FOUND,false,null);
    }

}
