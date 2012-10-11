package auctionsniper.xmpp

class XMPPAuctionException(message: String, cause: Exception)
  extends RuntimeException(message, cause) {
}
