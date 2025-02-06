<!DOCTYPE html>

<html>
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

	<title>User State Service</title>
  	<link rel="icon" href="/uss/favicon.ico" type="image/x-icon"/>
  	
  	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://getbootstrap.com/docs/5.3/assets/css/docs.css" rel="stylesheet">
  
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
</head>
<body>
<div class="container container-fluid">
	<h1>User State Service</h1>
	<p>
	<#if accesstoken??>
          <ul class="list-group">
		  <li class="list-group-item list-group-item-primary">Total users imported: ${app_state.stats_total_users_imported}</li>
		  <li class="list-group-item list-group-item-success">Recent logins: ${number_of_recent_logins}</li>
		  <li class="list-group-item list-group-item-danger">Recent deleted users: ${number_of_recent_deleted_users}</li>
		  <li class="list-group-item list-group-item-info">Total old users coming back: ${app_state.stats_number_of_old_users_comming_back}</li>
		  <li class="list-group-item list-group-item-warning">Total old users detected: ${app_state.stats_number_of_old_users_detected}</li>
		  <li class="list-group-item list-group-item-dark">Total old users removed: ${app_state.stats_number_of_old_users_removed}</li>
		  <li class="list-group-item list-group-item-secondary">Total email sent: ${app_state.stats_number_of_mails_sent}</li>
		  <li class="list-group-item list-group-item-light">
		  <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#mail-sending">
  				Test sending email
		  </button>
		  
		
		  </li>
  		  
		  
		   </ul>
		   
		   <#if response_test_mail_sending??>
			    <#assign ok = (response_test_mail_sending == 'Send succeeded')>
			    	
			    <div class="alert alert-dismissible fade show ${ok?then('alert-success', 'alert-danger')}" role="alert">
					 ${response_test_mail_sending}
					 <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
				</div>
					

		   </#if>
		   <!-- Modal -->
			<div class="modal fade" id="mail-sending" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
			  <div class="modal-dialog modal-dialog-centered" role="document">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title" id="exampleModalLongTitle">Test mailing</h5>
			      
			      </div>
			      <div class="modal-body">
			        
			      <form id="test_mail_sending_form" name="test_mail_sending_form" action="/uss/?accesstoken=${accesstoken}" method="post">
					  <div class="form-group">
					    <label for="email">Your email address</label>
					    <input type="email" class="form-control" id="email" name="email" placeholder="Enter email" required>
					  </div>
			  
					   <#if templatefilename??>
						<div class="form-group">
						    <label for="templatefilename">Use template file name</label>
						    <input type="text" class="form-control" id="templatefilename" name="templatefilename" readonly>
						  </div>
						  
					  <#else>		  
						  <div class="form-group">
						    <label for="message">Use default message</label>
						    <textarea class="form-control" id="message" rows="3" name="message" required>${message}</textarea>
						  </div>
					  </#if>
					 
					 <div class="form-group">
						    <label for="templateparams">Template params</label>
						    <textarea class="form-control" id="templateparams" rows="3" name="templateparams" required>${templateparams}</textarea>
				     </div>
						    
				    
			  		<input name="accesstoken" type="hidden" value="${accesstoken}">
		      		
		      		
				</form>
			
			        
			        
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
			        <button type="submit" class="btn btn-primary" form="test_mail_sending_form" >Send</button>
			      </div>
			    </div>
			  </div>
			</div>
		   
		   
    <#else>
    	Attach the provided accesstoken to view the statistics data    
    </#if>
	<p>
</div>

	<script>
    if ( window.history.replaceState ) {
 		 window.history.replaceState( null, null, window.location.href );
	}
	</script>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.3/dist/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
</body>
</html>

