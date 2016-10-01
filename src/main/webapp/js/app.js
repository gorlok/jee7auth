angular
	.module('myapp', ['ngMessages'])
	.controller('MyController', MyController)
	.constant('URL', 'http://localhost:8080/jee7auth/rest')
	.factory('dataFactory', dataFactory)
	.factory('authFactory', authFactory)
	.factory('authHttpRequestInterceptor', authHttpRequestInterceptor)
	.config(function ($httpProvider) {
		$httpProvider.interceptors.push('authHttpRequestInterceptor');
	});

MyController.$inject = ['dataFactory', 'authFactory'];
function MyController(dataFactory, authFactory) {
	var vm = this;
	vm.error = {};
	vm.user = '';
	vm.passw = '';
	
	vm.clearMessages = function() {
		vm.error = {};
	}
	
	vm.login = function() {
		vm.clearMessages();
		dataFactory.login(vm.user, vm.passw)
			.then(function(response){
				console.log('login success! token: ' + response.data);
				console.log('HEADERS: ', response.headers());
				console.log('JWT Access Token:' + (response.headers()['authorization'].replace(/Bearer /g,'')));
				authFactory.setAccessToken(response.headers()['authorization'].replace(/Bearer /g,''));
				vm.user = '';
				vm.passw = '';
			},
			function(response) {
				vm.error.message = 'Login error';
				console.log('login error. Response:' + response.data);
			});
	};
	
	vm.logout = function() {
		authFactory.reset();
	}
	
	vm.testGet = function() {
		dataFactory.testGet()
			.then(function(response){
				vm.clearMessages();
				console.log('GET OK: ' + response.data);
			},
			function(response) {
				console.log('GET ERROR. Response:' + response.data);
			});
	};
	
	vm.testDelete = function() {
		vm.clearMessages();
		dataFactory.testDelete()
			.then(function(response){
				console.log('DELETE OK: ' + response.data);
			},
			function(response) {
				vm.error.delete_message = 'Delete error. Permission denied.'
				console.log('DELETE ERROR. Response:' + response.data);
			});
	};
	
	vm.isAuthenticated = function() {
		var token = authFactory.isAuthenticated();
		if (token) {
			var decoded = jwt_decode(token);
			console.log(decoded);			
		}
		
		return token;
	}
}

dataFactory.$inject = ['$http', 'URL', 'authFactory'];
function dataFactory($http, URL, authFactory) {
	var factory = {};
	factory.login = function(user, passw) {
		authFactory.reset();
		return $http.post(URL + '/authentication', {"username":user,"password":passw});
	}
	
	factory.testGet = function() {
		return $http.get(URL + '/123');
	}
	
	factory.testDelete = function() {
		//return $http.delete(URL + '/123', {'headers': {'Authorization' : 'Bearer ' + authFactory.getAccessToken()}});
		return $http.delete(URL + '/123');
	}
	
	return factory;
}

authHttpRequestInterceptor.$inject = ['authFactory'];
function authHttpRequestInterceptor(authFactory) {
    return {
        request: function ($request) {
            if (authFactory.isAuthenticated()) {
                $request.headers['Authorization'] = 'Bearer ' + authFactory.getAccessToken();
            }
            return $request;
        }
    }
}

function authFactory() {
	var auth = {};
	
	auth.accessToken = '';
	auth.setAccessToken = function(token) {
		auth.accessToken = token;
	}
	auth.getAccessToken = function() {
		return auth.accessToken;
	}
	auth.isAuthenticated = function() {
		return auth.accessToken;
	}
	auth.reset = function() {
		auth.accessToken = '';
	}
	return auth;
}