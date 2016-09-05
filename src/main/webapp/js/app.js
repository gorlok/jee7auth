angular
	.module('myapp', [])
	.controller('MyController', MyController)
	.constant('URL', 'http://localhost:8080/jee7auth/rest')
	.factory('dataFactory', dataFactory)
	.factory('authFactory', authFactory);

MyController.$inject = ['dataFactory', 'authFactory'];
function MyController(dataFactory, authFactory) {
	var vm = this;
	vm.hello = 'Hello World!';
	vm.user = '';
	vm.passw = '';
	
	vm.login = function() {
		dataFactory.login(vm.user, vm.passw)
			.then(function(response){
				console.log('login success! token: ' + response.data);
				console.log('HEADERS: ', response.headers());
				console.log('JWT:' + (response.headers()['authorization'].replace(/Bearer /g,'')));
				authFactory.setJwt(response.headers()['authorization'].replace(/Bearer /g,''));
			},
			function(response) {
				console.log('login error. Response:' + response.data);
			});
	};
	
	vm.testGet = function() {
		dataFactory.testGet()
			.then(function(response){
				console.log('GET OK: ' + response.data);
			},
			function(response) {
				console.log('GET ERROR. Response:' + response.data);
			});
	};
	
	vm.testDelete = function() {
		dataFactory.testDelete()
			.then(function(response){
				console.log('DELETE OK: ' + response.data);
			},
			function(response) {
				console.log('DELETE ERROR. Response:' + response.data);
			});
	};
	
}

dataFactory.$inject = ['$http', 'URL', 'authFactory'];
function dataFactory($http, URL, authFactory) {
	var factory = {};
	
	factory.login = function(user, passw) {
		authFactory.setJwt('');
		return $http.post(URL + '/authentication', {"username":user,"password":passw});
	}
	
	factory.testGet = function() {
		return $http.get(URL + '/123');
	}
	
	factory.testDelete = function() {
		return $http.delete(URL + '/123', {'headers': {'Authorization' : 'Bearer ' + authFactory.getJwt()}});
	}
	
	return factory;
}

function authFactory() {
	var factory = {};
	
	factory.jwt = '';
	factory.setJwt = function(_jwt) {
		factory.jwt = _jwt;
	}
	factory.getJwt = function() {
		return factory.jwt;
	}
	
	return factory;
}