<!DOCTYPE html>
<html>
  <head>
    <title>
      Sign-In Sample
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
     </script>
    <?php echo isset($head) ? $head : ""; ?>
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
  </head>
  <body>
    <?php echo $content; ?>
    <script type="text/javascript">
 (function() {
      var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
      po.src = 'https://apis.google.com/js/client:plusone.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
    })();
    </script>
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
  </body>
</html>