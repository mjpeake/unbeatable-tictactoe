# Play the original Java game on GitHub Pages

The `docs/` folder contains a ready-to-publish CheerpJ wrapper and a JAR compiled
from the original Java sources. No Java installation is required for visitors.
The runtime loads from Leaning Technologies' CDN when Play game is clicked.

## Publish

1. Add `docs/`, `build-web.ps1`, `.gitignore`, and this guide to the existing repository and commit/push them.
2. Open the repository's **Settings → Pages**.
3. Select **Deploy from a branch**, choose your default branch and **/docs**, then Save.
4. Once GitHub's Pages deployment finishes, open the URL shown in Settings.
   Normally it is `https://mjpeake.github.io/unbeatable-tictactoe/`; a custom
   domain on your main site may change the hostname.
5. Link that URL from your portfolio.

## Rebuild after Java changes

On Windows with a JDK 9 or newer:

```powershell
./build-web.ps1
```

Commit the rebuilt `docs/UnbeatableTicTacToe.jar` along with source changes.
Compilation targets Java 8, matching the runtime selected in `docs/game.js`.

On Linux/macOS with a JDK 9 or newer:

```sh
mkdir -p build/web-classes
javac --release 8 -d build/web-classes src/*.java
jar --create --file docs/UnbeatableTicTacToe.jar --main-class TicTacToe -C build/web-classes .
```

## Local preview

Serve the repository over HTTP (do not double-click the HTML file):

```sh
npx http-server -p 8080
```

Open `http://localhost:8080/docs/`. Node.js is needed for this preview command.
Use a server with HTTP Range support; Python's basic http.server lacks it.
Internet access is needed to load CheerpJ.
The wrapper scales the original board to fit smaller screens. A browser-only Java property hides the desktop window title bar; normal desktop launches keep it.
Scores reset on a page reload, as they do when restarting the desktop game.

CheerpJ attribution is included in the page. Its Community License permits
personal projects using the hosted runtime; see https://cheerpj.com/docs/licensing.
