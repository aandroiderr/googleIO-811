<!DOCTYPE html>
<html>
  <head>
    <title>
      Sign-In Sample
    </title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
     </script>
    <?php echo isset($head) ? $head : ""; ?>
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
  </body>
</html>
