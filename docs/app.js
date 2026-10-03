const play = document.getElementById('play');
const launchTitle = document.getElementById('launch-title');
const note = document.getElementById('launch-note');
const display = document.getElementById('display');
const frame = document.querySelector('.demo-frame');
const launch = document.getElementById('launch');
let gameFrame;
let slowLoad;
let retryTimer;

// Isolate Java's viewport from the page scale so Swing retains its native dimensions.
new ResizeObserver(() => {
  display.style.transform = `scale(${frame.clientWidth / 670})`;
}).observe(frame);

window.addEventListener('message', event => {
  if (event.origin !== location.origin || event.source !== gameFrame?.contentWindow || event.data?.type !== 'tictactoe') return;
  if (event.data.state === 'runtime' || event.data.state === 'starting') {
    note.textContent = event.data.state === 'runtime'
      ? 'Preparing Java. The first launch can take around a minute.'
      : 'Java is ready. Opening the board…';
    return;
  }
  clearTimeout(slowLoad);
  clearTimeout(retryTimer);
  frame.removeAttribute('aria-busy');
  if (event.data.state === 'ready') {
    launch.hidden = true;
    display.style.visibility = 'visible';

  } else if (event.data.state === 'error' || event.data.state === 'closed') {
    display.hidden = true;
    launch.hidden = false;
    launchTitle.textContent = event.data.state === 'error' ? 'Let’s try again.' : 'Another round?';
    play.disabled = false;
    play.textContent = event.data.state === 'error' ? 'Try again ↗' : 'Play again ↗';
    note.textContent = event.data.state === 'error' ? 'Check your connection, then try again.' : 'Start a fresh game.';

  }
});

play.addEventListener('click', () => {
  clearTimeout(slowLoad);
  clearTimeout(retryTimer);
  play.disabled = true;
  play.textContent = 'Loading…';
  note.textContent = 'The first visit takes a little longer.';

  launchTitle.textContent = 'Getting ready.';
  frame.setAttribute('aria-busy', 'true');
  gameFrame?.remove();
  gameFrame = document.createElement('iframe');
  gameFrame.title = 'Playable Tic Tac Toe board';
  gameFrame.width = 670;
  gameFrame.height = 660;
  gameFrame.style.cssText = 'display:block;border:0;width:670px;height:660px';
  gameFrame.src = 'game.html?v=5';
  display.hidden = false;
  display.style.visibility = 'hidden';
  display.appendChild(gameFrame);
  slowLoad = setTimeout(() => {
    note.textContent = 'Still preparing the game. You can leave this page open.';
  }, 20000);
  retryTimer = setTimeout(() => {
    play.disabled = false;
    play.textContent = 'Retry loading ↗';
    note.textContent = 'Taking longer than expected. Keep waiting, or retry.';
  }, 60000);
});
