package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.ManagerService;
import swp.auctionkoi.service.user.impl.ManagerServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerServiceImpl managerService;

    @PostMapping
    public ResponseEntity<User> addStaff(@RequestBody UserCreateRequest request) {

        Optional<User> user = managerService.addStaff(request);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }
}
