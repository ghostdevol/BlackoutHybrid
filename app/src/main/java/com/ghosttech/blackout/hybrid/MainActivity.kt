class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var wallet: WalletContext

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallet = WalletContext(this)

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.addJavascriptInterface(BridgeInterface(wallet), "Blackout")

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.loadUrl("file:///android_asset/index.html")
    }
}