package main.db;
import java.util.ArrayList;
import java.util.List;
import main.model.User;

public class UserDatabase {
    private static UserDatabase instance = new UserDatabase();
    private List<User> userList = new ArrayList<>();

    private UserDatabase() {
        // 테스트용 기본 계정 생성 (바로 로그인 테스트 가능)
        userList.add(new User("admin", "1234", "01012345678", "우주 정거장 1호"));
    }

    public static UserDatabase getInstance() {
        return instance;
    }

    // 회원가입
    public boolean register(User newUser) {
        for (User u : userList) {
            if (u.getId().equals(newUser.getId())) {
                return false; // 아이디 중복
            }
        }
        userList.add(newUser);
        return true;
    }

    // 로그인 확인
    public User login(String id, String password) {
        for (User u : userList) {
            if (u.getId().equals(id) && u.getPassword().equals(password)) {
                return u; // 성공 시 해당 유저 반환
            }
        }
        return null; // 실패
    }

    // 정보 업데이트
    public void update(User updatedUser) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(updatedUser.getId())) {
                userList.set(i, updatedUser);
                break;
            }
        }
    }
}