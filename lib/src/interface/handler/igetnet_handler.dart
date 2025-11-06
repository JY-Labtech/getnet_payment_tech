//Contract payment handler
abstract class IGetnetHandler {
  Future<void> onTransactionSuccess();

  Future<void> onError(String message);

  Future<void> onMessage(String message);

  Future<void> onChanged(String message);

  Future<void> onFinishedResponse(String message);

  Future<void> onLoading(bool show);
}

