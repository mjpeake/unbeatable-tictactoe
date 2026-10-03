(async () => {
  function report(state) { parent.postMessage({type: 'tictactoe', state}, location.origin); }
  try {
    await new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://cjrtnc.leaningtech.com/4.3/loader.js';
      script.onload = resolve;
      script.onerror = () => reject(new Error('Runtime download failed.'));
      document.head.appendChild(script);
    });
    report('runtime');
    await cheerpjInit({
      version: 8,
      javaProperties: ['tictactoe.web=true'],
      natives: { async Java_TicTacToe_browserReady() { report('ready'); } }
    });
    cheerpjCreateDisplay(670, 660, document.getElementById('game'));
    report('starting');
    const jar = new URL('UnbeatableTicTacToe.jar', document.baseURI);
    const exitCode = await cheerpjRunJar('/app' + jar.pathname);
    report(exitCode === 0 ? 'closed' : 'error');
  } catch (error) {
    console.error(error);
    report('error');
  }
})();
