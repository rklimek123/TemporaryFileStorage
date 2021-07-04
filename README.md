# TemporaryFileStorage
Final project made during the JNP2 - Architecture and Integration of IT Systems course on MIMUW

## Uruchamianie

Aby uruchomić projekt, należy do pliku `application.properties` w folderze głównym dopisać wartości parametrów:

* `access.token`
* `client.id`
* `client.secret` - te 3 pola to dane do autentykacji konta Google.

Można również skonfigurować pozostałe parametry, w szczególności:
* `limit.extension` - rozszerzenie plików, które mają być wysyłane do GoogleDrive, np `".c"`. Pozostałe pliki będą zapisane w KafkaReceiver/send_receive.

Po każdej zmianie w tym pliku w folderze głównym należy wywołać skrypt `./copy_properties.sh`.

Następnie należy użyć komendy `gradle build`, używając pliku build.gradle dla każdego z trzech programów: KafkaSender, KafkaReceiver i ReceiverToGDrive. Klaster uruchamia się przy użyciu komendy `docker-compose up` w folderze głównym.

Proces ten można zautomatyzować przez użycie skryptu `./boot.sh`. Skrypt ten zatrzymuje i usuwa wszystkie kontenery dockera (uwaga, nie zawsze jest to pożądane zachowanie), usuwa stworzone obrazy tych trzech programów, jeśli są, a następnie buduje trzy programy, wykonuje `./copy_properties.sh` i na koniec wywołuje `docker-compose up`.

## Użycie

Po skonfigurowaniu i uruchomieniu programu, program prześle wszystkie pliki znajdujące się w folderze,
do którego prowadzi ścieżka `directory` wskazana w pliku konfiguracyjnym (rozpatrywana względem kontenera z programem KafkaSender), do Kafki.
Następnie będzie nasłuchiwał, czy nie pojawiły się inne pliki w tym folderze. Jeśli tak, przetworzy je i jeśli rozszerzenia będą się zgadzać, prześle je.
Każdy przetworzony przez KafkaSender plik jest usuwany z folderu.

Z Kafki, wysłany plik odbierają dwa programy: KafkaReceiver i ReceiverToGDrive.

ReceiverToGDrive sprawdza, czy otrzymany plik ma odpowiednie rozszerzenie, zgadzające się z parametrem `limit.extension`.
Jeśli  tak, przesyła ten plik na konto GoogleDrive użytkownika wskazanego w parametrach dotyczących autentykacji.

KafkaReceiver sprawdza, czy otrzymany plik ma odpowiednie rozszerzenie, różne od rozszerzenia z parametru `limit.extension`.
Jeżeli tak, zapisuje ten plik w folderze, do którego prowadzi ścieżka `directory` wskazana w pliku konfiguracyjnym
(rozpatrywana względem kontenera z programem KafkaReceiver).

Takie połączenie tych programów tworzy aplikację, która pozwala wysyłać plik na GoogleDrive jeżeli ma odpowiednie rozszerzenie lub przechowywać go na dysku w innym wypadku (w metaforycznym śmietniku).

Oczywiście aplikację można rozwijać dodając nowych senderów, albo nowych receiverów podpiętych do Kafki.
