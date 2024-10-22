package swp.auctionkoi.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.VNPAYService;



//@RequestMapping("/vnpay")
@Controller
public class VNPAYController {

    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping({"", "/"})
    public String home() {
        return "createOrder";
    }

    // Chuyển hướng người dùng đến cổng thanh toán VNPAY
    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("memberId") String memberId,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, memberId, baseUrl);
        return "redirect:" + vnpayUrl;
    }




        // Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
        @GetMapping("/vnpay-payment-return")
        public String paymentCompleted(HttpServletRequest request, Model model) {
            int paymentStatus = vnPayService.orderReturn(request);
            System.out.println("Payment Status: " + paymentStatus);

            String memberId = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");
            System.out.println("Member ID: " + memberId);
            System.out.println("Payment Time: " + paymentTime);
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Total Price: " + totalPrice);

            Double amount = Double.valueOf(totalPrice) / 100;  // Chia giá trị cho 100

            if (paymentStatus == 1) {
                Wallet wallet = walletRepository.findByUserId(Integer.valueOf(memberId))
                        .orElseThrow(() -> new RuntimeException("Wallet not found"));
                if (wallet != null) {
                    wallet.setBalance(wallet.getBalance() + amount);
                    walletRepository.save(wallet);

                    Transaction transaction = new Transaction();
                    transaction.setMember(wallet.getMember());
                    transaction.setWalletId(wallet.getId());
                    transaction.setTransactionType(TransactionType.TOP_UP);
                    transaction.setTransactionFee(0.0);
                    transaction.setPaymentId(Integer.valueOf(transactionId));

                    transactionRepository.save(transaction);
                }

                model.addAttribute("orderId", memberId);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("paymentTime", paymentTime);
                model.addAttribute("transactionId", transactionId);

                return "orderSuccess";
            } else {
                return "orderFail";
            }
        }
}
