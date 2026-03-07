package vn.dwchwang.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Subscriber;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.service.SubscriberService;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

  private final SubscriberService subscriberService;

  public SubscriberController(SubscriberService subscriberService) {
    this.subscriberService = subscriberService;
  }

  @PostMapping("/subscribers")
  @ApiMessage("Create a subscriber")
  public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
    if (this.subscriberService.existsByEmail(subscriber.getEmail())) {
      throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
  }

  @PutMapping("/subscribers")
  @ApiMessage("Update a subscriber")
  public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
    Subscriber updated = this.subscriberService.update(subscriber);
    if (updated == null) {
      throw new IdInvalidException("Subscriber id " + subscriber.getId() + " không tồn tại");
    }
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/subscribers/{id}")
  @ApiMessage("Delete a subscriber")
  public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) throws IdInvalidException {
    Optional<Subscriber> subscriber = this.subscriberService.findById(id);
    if (subscriber.isEmpty()) {
      throw new IdInvalidException("Subscriber id " + id + " không tồn tại");
    }
    this.subscriberService.delete(id);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/subscribers")
  @ApiMessage("Fetch all subscribers")
  public ResponseEntity<ResultPaginationDTO> getAllSubscribers(
      @Filter Specification<Subscriber> spec, Pageable pageable) {
    return ResponseEntity.ok(this.subscriberService.fetchAll(spec, pageable));
  }

  /**
   * POST /subscribers/skills — returns skills of the current logged-in subscriber
   */
  @PostMapping("/subscribers/skills")
  @ApiMessage("Get subscriber skills")
  public ResponseEntity<Subscriber> getSubscriberSkills() {
    String email = SecurityUtil.getCurrentUserLogin().orElse("");
    Subscriber subscriber = this.subscriberService.findByEmail(email);
    return ResponseEntity.ok(subscriber);
  }
}
