package com.jhhc.baseframework.test;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 *
 * @author yecq
 */
@Component("make_logged_advice")
public class MakeLoggedAdvice {

    public void addSession(JoinPoint p) {
        Object[] o = p.getArgs();
        HttpSession session = null;
        for (int i = 0; i < o.length; i++) {
            if (o[i] instanceof HttpSession) {
                session = (HttpSession) o[i];
                break;
            }else if (o[i] instanceof HttpServletRequest) {
                session = ((HttpServletRequest) o[i]).getSession();
                break;
            }
        }
        if (session == null) {
            return;
        } else {
            // 取出username
            //好办法
            MethodSignature sign = (MethodSignature) p.getSignature();
            Method m = sign.getMethod();
            if (m.isAnnotationPresent(MakeLogged.class)) {
                // 获取注解的参数
                MakeLogged noti = m.getAnnotation(MakeLogged.class);
                String username = noti.value()[0];
                session.setAttribute("username", username);
//                LoginManager.getInstance().clear();
//                new Login(username, passwd).login();
            }
        }
    }
}
